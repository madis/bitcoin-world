# Bitcoin World

A project to configure and run various Bitcoin and Lightning services for development and testing.

A lot of what follows is based on the great tutorials on https://lnroom.live/

# Setup

## Babashka

The helper scripts are implemented in Clojure (LISP) running using [Babashka](https://github.com/babashka/babashka)
To run them have `bb` binary on your `$PATH` and then use

Usage:

```bash
# Get a list of tasks
bb tasks
# Run commands for core lightning node 1
bb c-lightning-cli node-1 listfunds
```

## Overmind

To start everything up (bitcoin-core, core lightning daemon for 2 nodes, lnsocket-proxy, etc), [Overmind](https://github.com/DarthSim/overmind) is used
The processes are defined in `Procfile`

Usage: `overmind start`

## Bitcoin Core

The base of all the following is Bitcoin Core, needed to run a node that mines blocks and adds new transactions to the blockchain.

Download from: https://bitcoin.org/bin/bitcoin-core-25.0/bitcoin-25.0-x86_64-linux-gnu.tar.gz
Location: `apps/bitcoin-core`

## Core Lightning

Download from: https://github.com/ElementsProject/lightning/releases/download/v23.11/clightning-v23.11-Ubuntu-22.04.tar.xz
Location: `apps/core-lightning`

To get `clnrest` plugin working [clnrest docs](https://docs.corelightning.org/docs/rest)
  - if other plugins (like Boltz's hold plugin) have Python dependencies, run this within that poetry env
    + `source /home/madis/.cache/pypoetry/virtualenvs/plugins-L4oZGMpY-py3.11/bin/activate`
  - `pip install -r ./apps/core-lightning/usr/libexec/c-lightning/plugins/clnrest/requirements.txt`

### Plugins

For HODL invoices [Boltz hold](https://github.com/BoltzExchange/boltz-backend/blob/master/tools/plugins/hold) plugin is used.
To install it:
1. Clone git@github.com:BoltzExchange/boltz-backend.git
2. Move or symlink `tools/plugins/hold` under `apps/core-lightning-plugins/hold`
3. Install the dependencies using poetry
  - under `apps/core-lightning-plugins/hold` call `poetry install`
4. Active python virtual environment to have the plugin dependencies available
  - `poetry shell`
  - or if you don't want new shell:
    ```bash
    source `poetry env info --path`/bin/activate

    ```
5. In this new or updated shell run the core lightning daemon as usual
