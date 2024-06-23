(ns streamcraft.entity.core
  (:require [malli.core :as m]
            [malli.registry :as mr]
            [malli.util :as mu]
            [streamcraft.entity.api :as-alias entity]))

(defonce registry
         (atom (merge (m/default-schemas)
                      (mu/schemas))))

(defn add-to-registry! [key schema]
  (swap! registry assoc key schema))

(defmacro defschema [name id schema]
  `(let [schema# ~schema]
     (def ~name schema#)
     (add-to-registry! ~id schema#)))

(defn as-entity [schema]
  (with-meta schema {::entity? true}))

(defmacro defentity [name id schema]
  `(defschema ~name ~id (as-entity ~schema)))

(def entity? (comp ::entity? meta))

(defn init-registry! []
  (mr/set-default-registry!
    (mr/mutable-registry registry)))

(defn entity-name
  [schema]
  (-> schema
      (m/properties)
      ::entity/name))


