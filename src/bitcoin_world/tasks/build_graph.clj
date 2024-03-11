(ns bitcoin-world.tasks.build-graph
  (:require
    [clojure.pprint :refer [pprint]]
    [babashka.process :refer [shell]]
    [babashka.fs :as fs]
    [clojure.string :as string]
    [bitcoin-world.tasks.utils :refer [project-root args->str]]
    [bitcoin-world.tasks.core-lightning :as cln]
    [bitcoin-world.tasks.bitcoin-core :as bitcoin-core]
    [clojure.edn :as edn]
    [bitcoin-world.topology :as topology]))


(defn calculate-port [starting-port node-index & [sub-index]]
  (+ starting-port
     (* 10 node-index)
     (or sub-index 0)))


(defn lightning-dir
  [root-path topology-name node-name]
  (str (fs/path root-path (name topology-name) (name node-name))))


(defn config-file-path
  [root-path topology-name node-name]
  (str (lightning-dir root-path topology-name node-name) "/config/config"))


(defn generate-config [node-name node-index starting-port lightning-dir]
  (let [node-name (name node-name)]
    [[:network "regtest"]
     [:alias node-name]
     [:lightning-dir lightning-dir]
     [:bitcoin-cli bitcoin-core/bitcoin-cli-path]
     [:bitcoin-datadir bitcoin-core/data-dir-path]
     [:addr (str "0.0.0.0:" (calculate-port starting-port node-index 0))]
     [:bitcoin-rpcuser "bitcoin"]
     [:bitcoin-rpcpassword "bitcoin"]
     ; --plugin=PATH_TO_PLUGIN [--rest-port=N] [--rest-protocol=http|https] [--rest-execmode=MODE]
     ; c-lightning-REST opttions
     [:plugin (fs/path project-root "apps/c-lightning-REST/clrest.js")]
     [:rest-port (calculate-port starting-port node-index 1)]
     [:rest-docport (calculate-port starting-port node-index 2)]
     [:rest-protocol "https"]
     [:rest-execmode "production"]
     [:rest-domain "devbox"]
     ; For clnrest https://docs.corelightning.org/docs/rest
     [:clnrest-port (calculate-port starting-port node-index 3)]
     [:clnrest-protocol "http"]
     [:clnrest-host "0.0.0.0"]
     ; Boltz hold invoice plugin
     [:plugin "/home/madis/bitcoin-world/apps/core-lightning-plugins/hold/plugin.py"]]))


(defn config->ini-string [config]
  (let [join-params (fn [[param-name param-value]] (str (name param-name) "=" param-value))]
    (->> config
      (map join-params ,,,)
      (clojure.string/join "\n" ,,,))))

(defn write-config! [config-path config-str]
  (spit config-path config-str))

(defn topology->configs
  [topology-definition]
  (let [topology-name (:name topology-definition)
        root-path (:root-path topology-definition)
        starting-port (:starting-port topology-definition)
        nodes (:nodes topology-definition)]
  (map-indexed
    (fn [idx node]
      [(config-file-path root-path topology-name (:name node))
        (config->ini-string
          (generate-config
            (:name node)
            (+ 1 idx)
            starting-port
            (lightning-dir root-path topology-name (-> node :name name))))])
    nodes)))

(defn init-topology
  [topology-config]
  (let []
    (doseq [[path config] (topology->configs topology-config)]
      (do
        (println "Writing config to:" path)
        (fs/create-dirs (fs/parent path))
        (write-config! path config)))))

(defn cli [[command config-path root-path]]
  (let [topology-config (edn/read-string (slurp config-path))
        final-config (if root-path
                       (assoc topology-config :root-path root-path)
                       topology-config)]
    (println ">>> bitcoin-world.tasks.build-graph/cli" {:command command :config-path config-path :root-path root-path })
    (pprint final-config)
    (case command
      "init" (init-topology final-config)
      (println "Unknown command: " command))))
