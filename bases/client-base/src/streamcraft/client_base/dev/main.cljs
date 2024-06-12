(ns streamcraft.client-base.dev.main
  (:require [hyperfiddle.electric :as e]
            [streamcraft.client-base.app :refer [App]]
            [streamcraft.web-app.main :as app]))

(def entrypoint (e/boot-client {} App nil))

(defn ^:dev/after-load ^:export start! []
  (app/start! entrypoint))

(defn ^:dev/before-load stop! []
  (app/stop!))
