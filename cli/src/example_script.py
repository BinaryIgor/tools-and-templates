from commons import meta, crypto
from commons.infra import resources

log = meta.new_log("example_script")

droplets = resources.get_droplets()

for d in droplets:
    print(d)