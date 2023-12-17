(ns bitcoin-world.tasks.bitcoin-core
  (:require
    [clojure.pprint :refer [pprint]]
    [babashka.process :refer [shell]]
    [babashka.fs :as fs]
    [clojure.string :as string]
    [bitcoin-world.tasks.utils :refer [project-root args->str]]))

(def config-path
  (fs/path project-root "config/bitcoin-core/bitcoin.conf"))

(def data-dir-path
  (fs/path project-root "data/bitcoin-core"))

(def bitcoind-path
  (fs/path project-root "apps/bitcoin-core/bitcoin-25.0/bin/bitcoind"))

(def bitcoin-cli-path
  (fs/path project-root "apps/bitcoin-core/bitcoin-25.0/bin/bitcoin-cli"))

(def bitcoind
  (args->str [bitcoind-path "-regtest" ["-datadir=" data-dir-path] ["-conf=" config-path]]))

(def bitcoin-cli
  (args->str [bitcoin-cli-path "-regtest" ["-conf=" config-path]]))

(defn cli [args]
  (let [command (str bitcoin-cli " " (string/join " " args))]
    (println ">>> Running bitcoin-cli:\n   " command)
    (println ">>> Result  bitcoin-cli:")
    (pprint (shell command))))

(defn daemon
  ([] (daemon []))
  ([args]
    (println ">>> Starting bitcoin-core" args)
    (shell bitcoind)))
