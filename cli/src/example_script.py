from commons import meta, crypto

log = meta.new_log("example_script")

cmd = meta.cmd_args(args_definitions={
    "package_path": {
        "required": True,
        "help": meta.multiline_description("Some required, ", "and long description")
    },
    "flag": {
        "action": "store_true"
    },
    "anti_flag": {
        "action": "store_true"
    }
})

package_path = cmd['package_path']
flag = cmd["flag"]
anti_flag = cmd["anti_flag"]

log.info(f"Example script with many params... package_path: {package_path}, flag: {flag}, anti_flag: {anti_flag}")
log.info(f"root dir: {meta.root_dir()}")
log.info(f"random secret: {crypto.random_key()}")