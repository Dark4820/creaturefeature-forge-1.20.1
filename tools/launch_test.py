r"""Starts the test instance (the Forge profile installed in %APPDATA%/.minecraft) offline, without a launcher, with
the real (production) Forge, not the dev runClient.
Usage: python -I tools/launch_test.py 1.20.1-forge-47.4.28 <game dir> <java17.exe> [-Xmx2G] [-Dprop=value ...] [game args,
e.g. --quickPlaySingleplayer "<world folder>"]. The -D... go to the JVM, the rest to the game.
For checks only (e.g. -Dcreaturefeature.selftest=true on a copy of a world); to play, use a normal launcher."""
import json, os, subprocess, sys

MC = os.path.join(os.environ["APPDATA"], ".minecraft")
vid, gamedir, java = sys.argv[1:4]
LIB = os.path.join(MC, "libraries")


def load(v):
    with open(os.path.join(MC, "versions", v, v + ".json"), encoding="utf-8") as f:
        return json.load(f)


def allowed(rules):
    if not rules:
        return True
    ok = False
    for r in rules:
        os_ = r.get("os", {})
        if "features" in r:
            continue
        match = (not os_.get("name") or os_["name"] == "windows") and (not os_.get("arch") or os_["arch"] == "x86")
        if os_.get("arch") == "x86":
            match = False
        if match:
            ok = r["action"] == "allow"
    return ok


child = load(vid)
parent = load(child["inheritsFrom"])
libs, seen = [], set()
for lib in child.get("libraries", []) + parent.get("libraries", []):
    if not allowed(lib.get("rules")):
        continue
    art = lib.get("downloads", {}).get("artifact")
    if art and art.get("path"):
        p = os.path.join(LIB, art["path"])
    else:
        parts = lib["name"].split(":")
        g, a, ver = parts[:3]
        cls = parts[3] if len(parts) > 3 else None
        if cls and cls.startswith("natives-") and cls != "natives-windows":
            continue
        p = os.path.join(LIB, *g.split("."), a, ver, f"{a}-{ver}-{cls}.jar" if cls else f"{a}-{ver}.jar")
    key = ":".join(lib["name"].split(":")[:2] + lib["name"].split(":")[3:])
    if key in seen:
        continue
    seen.add(key)
    if os.path.exists(p) and p not in libs:
        libs.append(p)
libs.append(os.path.join(MC, "versions", vid, vid + ".jar"))
natives = os.path.join(gamedir, "natives")
os.makedirs(natives, exist_ok=True)
sub = {
    "${library_directory}": LIB, "${classpath_separator}": ";", "${version_name}": vid,
    "${natives_directory}": natives, "${launcher_name}": "creaturefeature-test", "${launcher_version}": "1",
    "${classpath}": ";".join(libs), "${auth_player_name}": "Dev", "${game_directory}": gamedir,
    "${assets_root}": os.path.join(MC, "assets"), "${assets_index_name}": parent["assetIndex"]["id"],
    "${auth_uuid}": "00000000000000000000000000000001", "${auth_access_token}": "0",
    "${clientid}": "0", "${auth_xuid}": "0", "${user_type}": "legacy", "${version_type}": "release",
}


def expand(args):
    out = []
    for a in args:
        if isinstance(a, dict):
            if not allowed(a.get("rules")):
                continue
            v = a["value"]
            a_list = v if isinstance(v, list) else [v]
        else:
            a_list = [a]
        for s in a_list:
            for k, val in sub.items():
                s = s.replace(k, val)
            out.append(s)
    return out


xmx = next((a for a in sys.argv[4:] if a.startswith("-Xmx")), "-Xmx4G")  # an -Xmx... among the extra args replaces the default
jvm = [xmx] + expand(parent["arguments"]["jvm"]) + expand(child["arguments"].get("jvm", []))
game = expand(parent["arguments"]["game"]) + expand(child["arguments"].get("game", []))
extra = sys.argv[4:]  # -D... go to the JVM, the rest to the game (e.g. --quickPlaySingleplayer "New World")
jvm += [a for a in extra if a.startswith("-D")]
extra = [a for a in extra if not a.startswith("-Xmx")]
cmd = [java] + jvm + [child["mainClass"]] + game + [a for a in extra if not a.startswith("-D")]
log = open(os.path.join(gamedir, "launch-stdout.log"), "w", encoding="utf-8")
sys.exit(subprocess.call(cmd, cwd=gamedir, stdout=log, stderr=subprocess.STDOUT))
