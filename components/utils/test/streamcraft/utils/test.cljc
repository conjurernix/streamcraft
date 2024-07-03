(ns streamcraft.utils.test
  (:require [com.stuartsierra.component :as component]
            [streamcraft.entity.api :as entity]
    ;[streamcraft.persistence-xtdb.api :as db.xtdb]
            [streamcraft.persistence-datomic-pro.api :as db.datomic-pro]
            [streamcraft.migration-datomic.api :as migration.datomic]
            [streamcraft.persistence-schema-transformer-malli-datomic.api :as st.p.m.d])
  #?(:clj (:import (clojure.lang ExceptionInfo))))

; Xtdb

;(defn fresh-xtdb-persistence []
;  (db.xtdb/make-persistence))

; Datomic

(defn fresh-datomic-pro-persistence []
  (db.datomic-pro/make-persistence))

(defn fresh-datomic-migration []
  (migration.datomic/make-migration))

(defn fresh-malli-datomic-persistence-schema-transformer []
  (st.p.m.d/make-persistence-schema-transformer))

; Malli Registry

(defn fresh-entity-registry []
  (entity/make-registry))


; System

(def ^:dynamic *system* nil)

(defmacro with-system [system body]
  `(binding [*system* (component/start-system ~system)]
     ~@body
     (component/stop-system *system*)))

(defn with-system-fixture [system]
  (fn [f]
    (with-system system
      (f))))

(defmacro catch-thrown-info [f]
  `(try
     ~f
     (catch ExceptionInfo e#
       {:msg  (ex-message e#)
        :data (ex-data e#)})))

