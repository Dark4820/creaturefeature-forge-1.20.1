"""Copies and converts Creature Feature resources from the 1.21.1 source to the Forge 1.20.1 port.

Re-runnable: it only writes content whose creaturefeature ids are already registered in the port's Java code
(CFItemInit / CFBlockInit), so later phases just run it again. Hand-made files (atlases) are not touched.

Usage: python -I tools/convert_resources.py
"""
import json
import os
import re
import shutil
import sys

PORT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
REF = r"C:\cf\ref\creaturefeature-master\creaturefeature-master\src\main\resources"
JAVA = os.path.join(PORT, "src", "main", "java", "net", "atired", "creaturefeature")
OUT = os.path.join(PORT, "src", "main", "resources")
NS = "creaturefeature"

# Source-only content (★, decided in F9) and Sable content (removed, decision D2)
EXCLUDED_IDS = {
    "obelisk", "obelisk_inside", "obelisk_inventory", "powder_skull_spawn_egg", "fisheye", "red_powder",
    "runic_stone_bricks", "toadstool", "toadstool_spawn_egg", "ominous_powder_skull",
}
EXCLUDED_ASSET_PREFIXES = (
    "particles/", "shaders/",  # phase F2
)
STAR_ASSETS = {
    "sounds/fisheye_screech.ogg",
    "textures/block/obelisk.png", "textures/block/obelisk_outside.png",
    "textures/entity/evil.png", "textures/entity/fisheye.png", "textures/entity/fisheye_trail.png",
    "textures/entity/markapplier.png", "textures/entity/powder_skull.png", "textures/entity/powder_skull_eyes.png",
    "textures/entity/red_powder_bg.png", "textures/entity/trichael.png", "textures/entity/trichael_overlay.png",
    "textures/entity/trichael_screen.png", "textures/entity/wraith.png", "textures/entity/wraith_cuff.png",
}


def registered_ids():
    ids = set()
    for name in ("init/CFItemInit.java", "init/CFBlockInit.java"):
        path = os.path.join(JAVA, name)
        if os.path.exists(path):
            ids.update(re.findall(r'register(?:Block)?\(\s*"([a-z0-9_]+)"', open(path, encoding="utf-8").read()))
    return ids


def write_json(path, data):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        json.dump(data, f, indent=2, ensure_ascii=False)
        f.write("\n")


def own_refs(data):
    return set(re.findall(NS + r':([a-z0-9_/]+)', json.dumps(data)))


def missing(data, ids):
    return {r for r in own_refs(data) if "/" not in r and r not in ids}


# ---------- assets ----------
def copy_assets():
    src_root = os.path.join(REF, "assets", NS)
    dst_root = os.path.join(OUT, "assets", NS)
    for dirpath, _, files in os.walk(src_root):
        for fn in files:
            src = os.path.join(dirpath, fn)
            rel = os.path.relpath(src, src_root).replace("\\", "/")
            stem = os.path.splitext(fn)[0]
            if rel.startswith(EXCLUDED_ASSET_PREFIXES) or rel in STAR_ASSETS or stem in EXCLUDED_IDS:
                continue
            dst = os.path.join(dst_root, rel)
            os.makedirs(os.path.dirname(dst), exist_ok=True)
            if rel == "sounds.json":
                sounds = json.load(open(src, encoding="utf-8"))
                sounds.pop("entity.fisheye_screech", None)
                write_json(dst, sounds)
            else:
                shutil.copyfile(src, dst)


# ---------- data ----------
def convert_result(result):
    out = {"item": result["id"]}
    if result.get("count", 1) != 1:
        out["count"] = result["count"]
    return out


def convert_recipe(d):
    t = d["type"]
    if t in ("minecraft:crafting_shaped", "minecraft:crafting_shapeless"):
        d["result"] = convert_result(d["result"])
    elif t == "minecraft:stonecutting":
        res = d["result"]
        d["result"] = res["id"]
        d["count"] = res.get("count", 1)
    return d


def convert_data(ids):
    skipped = []
    data = os.path.join(REF, "data")
    out = os.path.join(OUT, "data")

    # recipes (1.21 "recipe" -> 1.20.1 "recipes")
    for fn in sorted(os.listdir(os.path.join(data, NS, "recipe"))):
        d = json.load(open(os.path.join(data, NS, "recipe", fn), encoding="utf-8"))
        if d["type"].startswith(NS + ":"):
            skipped.append(("recipe (special serializer, F6)", fn))
            continue
        miss = missing(d, ids)
        if miss:
            skipped.append(("recipe", fn + " needs " + ",".join(sorted(miss))))
            continue
        write_json(os.path.join(out, NS, "recipes", fn), convert_recipe(d))

    # block loot tables ("loot_table" -> "loot_tables")
    for fn in sorted(os.listdir(os.path.join(data, NS, "loot_table", "blocks"))):
        d = json.load(open(os.path.join(data, NS, "loot_table", "blocks", fn), encoding="utf-8"))
        if os.path.splitext(fn)[0] in EXCLUDED_IDS:
            continue
        miss = missing(d, ids)
        if miss:
            skipped.append(("block loot", fn + " needs " + ",".join(sorted(miss))))
            continue
        write_json(os.path.join(out, NS, "loot_tables", "blocks", fn), d)

    # block and item tags ("block" -> "blocks", "item" -> "items"); entity tags wait for the creatures
    tag_dirs = [
        (os.path.join(data, NS, "tags", "block"), os.path.join(out, NS, "tags", "blocks")),
        (os.path.join(data, "minecraft", "tags", "block"), os.path.join(out, "minecraft", "tags", "blocks")),
        (os.path.join(data, "minecraft", "tags", "item"), os.path.join(out, "minecraft", "tags", "items")),
    ]
    for src_dir, dst_dir in tag_dirs:
        for dirpath, _, files in os.walk(src_dir):
            for fn in files:
                rel = os.path.relpath(os.path.join(dirpath, fn), src_dir)
                d = json.load(open(os.path.join(dirpath, fn), encoding="utf-8"))
                values = []
                for v in d["values"]:
                    vid = v["id"] if isinstance(v, dict) else v
                    path = vid.split(":", 1)[1]
                    if vid.startswith(NS + ":") and path in EXCLUDED_IDS:
                        continue
                    if vid.startswith(NS + ":") and path not in ids:
                        skipped.append(("tag entry", rel + " -> " + vid))
                        continue
                    values.append(v)
                if values:
                    write_json(os.path.join(dst_dir, rel), {"replace": d.get("replace", False), "values": values})

    # trim materials (same folder and format in 1.20.1) and the Blueprint compat tag
    for fn in os.listdir(os.path.join(data, NS, "trim_material")):
        shutil.copyfile(os.path.join(data, NS, "trim_material", fn), os.path.join(out, NS, "trim_material", fn)) \
            if os.path.isdir(os.path.join(out, NS, "trim_material")) else (
            os.makedirs(os.path.join(out, NS, "trim_material")),
            shutil.copyfile(os.path.join(data, NS, "trim_material", fn), os.path.join(out, NS, "trim_material", fn)))
    bp = os.path.join("blueprint", "tags", "trim_material", "generates_overrides.json")
    os.makedirs(os.path.dirname(os.path.join(out, bp)), exist_ok=True)
    shutil.copyfile(os.path.join(data, bp), os.path.join(out, bp))
    return skipped


if __name__ == "__main__":
    ids = registered_ids()
    copy_assets()
    skipped = convert_data(ids)
    print(f"{len(ids)} registered ids")
    for kind, what in skipped:
        print(f"SKIPPED {kind}: {what}")
    sys.exit(0)
