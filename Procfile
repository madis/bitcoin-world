bitcoin_core: bb bitcoin-core
lightning_core_1: sleep 3 && bb c-lightning-daemon node-1
lightning_core_2: sleep 3 && bb c-lightning-daemon node-2
lnsocket_proxy: cd apps/lnsocket-proxy && HOST=0.0.0.0 PORT=3333 yarn start
c_lightning_REST_1: sleep 7 && cd apps/c-lightning-REST && node cl-rest.js node-1-cl-rest-config.json
c_lightning_REST_2: sleep 7 && cd apps/c-lightning-REST && node cl-rest.js node-2-cl-rest-config.json
