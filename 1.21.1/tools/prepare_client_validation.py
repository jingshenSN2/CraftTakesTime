"""Prepare a disposable client fixture after ./gradlew runGameTestServer."""
from pathlib import Path
import shutil
import json

root = Path(__file__).resolve().parents[1]
source = root / 'run/world'
target = root / 'run/saves/CTTValidation'
if not source.exists():
    raise SystemExit('Run ./gradlew runGameTestServer first.')
if target.exists():
    raise SystemExit('The test world already exists; use it or rename it before preparing a fresh fixture.')
shutil.copytree(source, target)
pack = target / "datapacks/ctt-client-fixture"
pack.mkdir(parents=True, exist_ok=True)
(pack / "pack.mcmeta").write_text(json.dumps({'pack': {'description': 'CTT disposable client integration fixture', 'pack_format': 48}}))
settings = pack / "data/crafttakestime/data_maps/entity_type/crafting_settings.json"
settings.parent.mkdir(parents=True, exist_ok=True)
settings.write_text(json.dumps({"values": {"minecraft:player": {"global_multiplier": 3}}}))
options_path = root / 'run/options.txt'
options = options_path.read_text() if options_path.exists() else ''
lines = [line for line in options.splitlines() if not line.startswith(('onboardAccessibility:', 'pauseOnLostFocus:'))]
lines.extend(['onboardAccessibility:false', 'pauseOnLostFocus:false'])
options_path.write_text('\n'.join(lines) + '\n')
print('Prepared CTTValidation in the project run folder. Run ./gradlew runClient -PclientIntegration')
