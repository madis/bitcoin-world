(ns bitcoin-world.topology
  (:require
    [clojure.spec.alpha :as s]))


(s/def ::data-root string?)
(s/def ::starting-port integer?)
(s/def ::name keyword?)
(def supported-implementations #{:c-lightning})
(s/def ::implementation #(supported-implementations %))
(s/def ::node (s/keys :req-un [::name ::implementation]))
(s/def ::nodes (s/coll-of ::node))

(s/def ::funds (s/tuple keyword? integer?))
(s/def ::from ::funds)
(s/def ::to ::funds)
(s/def ::channel (s/keys :req-un [::from ::to]))
(s/def ::channels (s/coll-of ::channel))

(s/def ::topology
  (s/keys :req-un
          [::data-root
           ::starting-port
           ::nodes
           ::channels]))
