(ns streamcraft.observability-mulog.core
  (:require [com.brunobonacci.mulog :as mu]
            [com.stuartsierra.component :as component]
            [streamcraft.protocols.api.observability :as observability]))

(defmacro -log [event args]
  `(mu/log ~event ~@args))

(defrecord ObservabilityMulog
  [config publishers ctx]

  component/Lifecycle
  (start [this]
    (let [{:keys [publishers ctx]} config]
      (doseq [publisher publishers]
        (mu/start-publisher! publisher))
      (-> this
          (assoc :publishers publishers)
          (assoc :ctx ctx))))

  (stop [this]
    (-> this
        (assoc :config nil)
        (assoc :publishers nil)
        (assoc :ctx nil)))

  observability/ILogging
  (log! [_ event data]
    (-log event (flatten (seq (assoc data :mulog/namespace 'random-namespace)))))

  (info! [this event data]
    (observability/log! this event (assoc data :level :info)))

  (warn! [this event data]
    (observability/log! this event (assoc data :level :warn)))

  (error! [this event data]
    (observability/log! this event (assoc data :level :error)))

  (debug! [this event data]
    (observability/log! this event (assoc data :level :debug))))


(defn make-observability []
  (map->ObservabilityMulog {}))
