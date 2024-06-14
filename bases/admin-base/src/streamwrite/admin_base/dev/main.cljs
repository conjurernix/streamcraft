(ns streamwrite.admin-base.dev.main
  (:require [hyperfiddle.electric :as e]
            [streamcraft.web-app.main :as app]
            [streamwrite.admin-base.app :refer [App]]))

(def entrypoint (e/boot-client {} App nil))

(defn ^:dev/after-load ^:export start! []
  (app/start! entrypoint))

(defn ^:dev/before-load stop! []
  (app/stop!))
