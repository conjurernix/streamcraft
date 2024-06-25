(ns streamcraft.utils.test
  (:require [com.stuartsierra.component :as component]
            [streamcraft.entity.api :as entity]
            [streamcraft.persistence-xtdb.api :as db.xtdb]
            [streamcraft.persistence-datomic-pro.api :as db.datomic-pro])
  #?(:clj (:import (clojure.lang ExceptionInfo))))

; Xtdb

(defn fresh-xtdb-persistence []
  (db.xtdb/make-persistence))

; Datomic

(defn fresh-datomic-pro-persistence []
  (db.datomic-pro/make-persistence))

; Malli Registry

(defn fresh-entity-registry []
  (entity/make-registry))

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

