(ns bitcoin-world.build-graph-test
  (:require
    [clojure.test :refer [deftest is testing]]
    [clojure.string :as s]
    [bitcoin-world.tasks.build-graph :as gra]
    [babashka.fs :as fs]
    [clojure.pprint :refer [pprint]]))

(defn get-in-pair-vector
  [coll key-to-find]
  (reduce (fn [_ [k v]]
            (if (= k key-to-find)
              (reduced v)))
          coll))

(deftest config-generation
  (testing "config generation"
    (let [node-name :alice
          generated-config (gra/generate-config node-name 1 7000 "/tmp/ze-lightning-dir")]
      (is (clojure.string/starts-with? (get-in-pair-vector generated-config :lightning-dir)
                                     "/tmp/ze-lightning-dir"))))

  (testing "config format"
    (is (= "hello=World") (gra/config->ini-string [[:hello "World"]]))
    (is (= "hello=World\nanswer=42") (gra/config->ini-string [[:hello "World"] [:answer 42]]))))

(def topology-definition
  {:name :triangle
   :root-path "/home/madis/bitcoin-world/data/triangle"
   :starting-port 7000
   :nodes
   [{:name :alice   :implementation :c-lightning}
    {:name :bob     :implementation :c-lightning}
    {:name :charlie :implementation :c-lightning}]

   :channels
   [{:from [:alice 1000000]   :to [:bob 0]}
    {:from [:bob 2000000]     :to [:charlie 0]}
    {:from [:charlie 3000000] :to [:alice 0]}]})

(deftest topology-tests
  (testing "init topology"
    (let [root-path "/tmp/megaroot"
          test-config (merge topology-definition {:root-path root-path})
          [[alice-path alice-config] [_ bob-config] _ ] (gra/topology->configs test-config)]
      (is (= (str root-path "/triangle/alice/config/config") alice-path))
      (is (s/includes? alice-config "network=regtest"))
      (is (s/includes? alice-config "alias=alice"))
      (is (s/includes? bob-config "alias=bob"))))

  (testing "start topology"))
