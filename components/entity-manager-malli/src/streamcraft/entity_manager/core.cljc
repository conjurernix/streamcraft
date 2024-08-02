(ns streamcraft.entity-manager.core
  (:require [com.stuartsierra.component :as component]
            [malli.core :as m]
            [malli.error :as me]
            [malli.generator :as mg]
            [malli.util :as mu]
            [potpuri.core :as pt]
            [streamcraft.protocols.api.entity-manager :as em]
            [taoensso.timbre :as log]))

(def EntityProps
  [:map
   [::em/name :string]
   [::em/id :keyword]
   [::em/key :keyword]])

(defrecord MalliEntityManager [schemas registry]
  component/Lifecycle

  (start [this]
    (log/info "Starting MalliEntityManager")
    (-> this
        (assoc :registry (merge
                           (m/default-schemas)
                           (mu/schemas)
                           schemas))))

  (stop [this]
    (log/info "Stopping MalliEntityManager")
    (-> this
        (assoc :schemas nil)
        (assoc :registry nil)))

  em/EntityManager

  (get-registry [_] registry)

  (get-entities [this]
    (->> schemas
         (pt/filter-vals #(em/entity? this %))))

  (merge-registry [this new-schemas]
    (-> this
        (update :schemas merge new-schemas)))

  (validate [_this schema data]
    (when-not (m/validate schema data {:registry registry})
      (let [explanation (m/explain schema data {:registry registry})]
        (throw (ex-info "Validation Error" {:error-reason (me/humanize explanation)
                                            :error-value  (me/error-value explanation)}))))
    data)


  (generate [_this schema]
    (mg/generate schema {:registry registry}))

  (name [_this schema]
    (-> schema
        (m/properties {:registry registry})
        ::em/name))

  (properties [_this schema]
    (m/properties schema {:registry registry}))

  (of-type [_this schema]
    ; If a collection schema (vector, set, sequential), return the first child schema
    (if (contains? #{:vector :set :sequential} (m/type schema {:registry registry}))
      (-> schema
          (m/children {:registry registry})
          (first)
          (m/type {:registry registry}))
      (m/type schema {:registry registry})))

  (cardinality [this schema]
    (or (-> this
            (em/properties schema)
            ::em/cardinality)
        (if (contains? #{:vector :set :sequential} (m/type schema {:registry registry}))
          :many
          :one)))

  (entity? [this schema]
    (m/validate EntityProps (em/properties this schema)))

  (entity-id-key [this schema]
    (or (-> this
            (em/properties schema)
            ::em/id)
        (throw (ex-info "Entity schema does not have an id key, or it's not an entity schema" {:schema schema}))))

  (entity-id [this schema value]
    (get value (em/entity-id-key this schema)))

  (select-entity-keys [_this schema entity]
    (-> schema
        (m/explicit-keys {:registry registry})
        (->> (select-keys entity))))
  (optional-keys [_this schema]
    (mu/optional-keys schema {:registry registry}))
  (optional-keys [_this schema keys]
    (mu/optional-keys schema keys {:registry registry})))


(defn make-entity-manager []
  (map->MalliEntityManager {}))