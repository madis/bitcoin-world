(ns bitcoin-world.tasks.utils
  (:require
    [clojure.string :as string]
    [babashka.fs :as fs]))

(def project-root
  (fs/cwd))

(defn args->str [args-vec]
  (string/join " " (map #(if (vector? %) (string/join "" %) %) args-vec)))
