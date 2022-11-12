# CLI

CLI to automate all kinds of operations.

## Requirements

* non-root docker access for current user
* bash interpreter
* python >= 3.8

## Setup

### Dependencies:

```
bash setup_cli.bash
```

### Activate (always before running scripts):

```
source venv/bin/activate
cd scripts
```

Always run scripts from **scripts dir**, they might not work otherwise.

Additionally, only for prod/another remote env:

```
export CLI_SECRETS_PASSWORD=<password for encrypted secrets file, expected under path: $HOME/.cli/secrets>
```

For local env, secrets are taken from config/local-secrets as plain text. Use appropriate argument for scripts, for
example:

```
python3 build_app.py --env prod --app_name prometheus
```

## Logs
Just use less:
* f, b (page up, page down)
* j, k (line up, line down)
* /<pattern> search

With command: /Logs: 2022-05-25(.*)
```
Logs: 2022-05-25T01:00:21 - 2022-05-25T01:00:44

2022-05-25 01:00:34,668:INFO:system_scheduler_app:Checking jobs to run...
2022-05-25 01:00:34,669:INFO:system_scheduler_app:All needed jobs run, sleeping


Logs: 2022-05-25T01:01:28 - 2022-05-25T01:01:50

2022-05-25 01:01:34,707:INFO:system_scheduler_app:Checking jobs to run...
2022-05-25 01:01:34,708:INFO:system_scheduler_app:All needed jobs run, sleeping


Logs: 2022-05-25T01:02:34 - 2022-05-25T01:02:56

2022-05-25 01:02:34,764:INFO:system_scheduler_app:Checking jobs to run...
2022-05-25 01:02:34,765:INFO:system_scheduler_app:All needed jobs run, sleeping


Logs: 2022-05-25T01:03:18 - 2022-05-25T01:03:41

2022-05-25 01:03:34,822:INFO:system_scheduler_app:Checking jobs to run...
2022-05-25 01:03:34,824:INFO:system_scheduler_app:All needed jobs run, sleeping


Logs: 2022-05-25T01:04:25 - 2022-05-25T01:04:47

2022-05-25 01:04:34,880:INFO:system_scheduler_app:Checking jobs to run...
2022-05-25 01:04:34,881:INFO:system_scheduler_app:All needed jobs run, sleeping


Logs: 2022-05-25T01:05:31 - 2022-05-25T01:05:53

2022-05-25 01:05:34,906:INFO:system_scheduler_app:Checking jobs to run...
2022-05-25 01:05:34,906:INFO:system_scheduler_app:All needed jobs run, sleeping


Logs: 2022-05-25T01:06:15 - 2022-05-25T01:06:38

2022-05-25 01:06:34,940:INFO:system_scheduler_app:Checking jobs to run...
2022-05-25 01:06:34,941:INFO:system_scheduler_app:All needed jobs run, sleeping


Logs: 2022-05-25T01:07:22 - 2022-05-25T01:07:44

2022-05-25 01:07:34,980:INFO:system_scheduler_app:Checking jobs to run...
2022-05-25 01:07:34,981:INFO:system_scheduler_app:All needed jobs run, sleeping


Logs: 2022-05-25T01:08:28 - 2022-05-25T01:08:50

2022-05-25 01:08:35,037:INFO:system_scheduler_app:Checking jobs to run...
2022-05-25 01:08:35,037:INFO:system_scheduler_app:All needed jobs run, sleepin
```

Similarly, you can check prefixes with /Logs: 2022-05-25T00 or ranges like:
/Logs: 2022-05-25T0[3-4] or /Logs: 2022-05-25T02:0[1-9] or