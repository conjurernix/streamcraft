(ns streamcraft.utils.test
  (:require [com.stuartsierra.component :as component]
            [malli.core :as m]
            [malli.generator :as mg]
            [malli.util :as mu]
            [streamcraft.entity.api :as entity]
            [streamcraft.persistence-xtdb.api :as db.xtdb])
  #?(:clj (:import (clojure.lang ExceptionInfo))))

; Entity
(defn set-up-registry [registry]
  (entity/init-registry!)
  (swap! entity/registry merge registry))

(defn tear-down-registry []
  (reset! entity/registry (merge (m/default-schemas)
                                 (mu/schemas))))

(defn with-registry [registry]
  (fn [f]
    (set-up-registry registry)
    (f)
    (tear-down-registry)))

(defn gen-entity [schema]
  (mg/generate schema))

; Xtdb

(defn fresh-xtdb-persistence []
  (db.xtdb/make-persistence))

; System

(def ^:dynamic *system* nil)

(defn with-system [system]
  (fn [f]
    (binding [*system* (component/start-system system)]
      (f)
      (component/stop-system system))))

(defmacro catch-thrown-info [f]
  `(try
     ~f
     (catch ExceptionInfo e#
       {:msg  (ex-message e#)
        :data (ex-data e#)})))