(ns bitcoin-world.tasks.core-lightning
  (:require
    [clojure.pprint :refer [pprint]]
    [babashka.process :refer [shell]]
    [babashka.fs :as fs]
    [clojure.string :as string]
    [bitcoin-world.tasks.bitcoin-core :as bitcoin-core]
    [bitcoin-world.tasks.utils :refer [project-root args->str]]))

(def data-dir
  (fs/path project-root "data/core-lightning"))

(def daemon-path
  (fs/path project-root "apps/core-lightning/usr/bin/lightningd"))

(def cli-path
  (fs/path project-root "apps/core-lightning/usr/bin/lightning-cli"))

(defn daemon-command [node]
  (let [[_ node-index] (clojure.string/split node #"-")
        port (+ 7000 (* 10 (Integer/parseInt node-index)))] ; node-1 => 7010, node-2 => 7020
    (args->str [daemon-path "--regtest"
                ["--lightning-dir=" (fs/path data-dir node)]
                ["--bitcoin-cli=" bitcoin-core/bitcoin-cli-path]
                ["--bitcoin-datadir=" bitcoin-core/data-dir-path]
                ["--addr=" (str "0.0.0.0:" port)]
                ["--bitcoin-rpcuser=" "bitcoin"]
                ["--bitcoin-rpcpassword=" "bitcoin"]
                ; --plugin=PATH_TO_PLUGIN [--rest-port=N] [--rest-protocol=http|https] [--rest-execmode=MODE]
                ; c-lightning-REST opttions
                ["--plugin=" (fs/path project-root "apps/c-lightning-REST/clrest.js")]
                ["--rest-port=" (+ port 1)]
                ["--rest-docport=" (+ port 2)]
                ["--rest-protocol=" "https"]
                ["--rest-execmode=" "production"]
                ["--rest-domain=" "devbox"]
                ; For clnrest https://docs.corelightning.org/docs/rest
                ["--clnrest-port=" (+ port 3)]
                ["--clnrest-protocol=" "http"]
                ["--clnrest-host=" "0.0.0.0"]
                ["--plugin=" "/home/madis/bitcoin-world/apps/core-lightning-plugins/hold/plugin.py"]
                ])))

(defn cli-command [node]
  (args->str [cli-path "--regtest"
              ["--lightning-dir=" (fs/path data-dir node)]]))

(defn daemon [node args]
  (println ">>> Core Lightning Daemon")
  (shell (daemon-command node)))

(defn cli [node args]
  (println ">>> Core Lightning CLI" args)
  (shell (str (cli-command node) " " (args->str args))))
