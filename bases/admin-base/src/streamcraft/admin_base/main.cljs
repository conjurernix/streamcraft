(ns streamcraft.admin-base.main
  (:require [streamcraft.web-app.app :as app]
            [streamcraft.admin-base.entrypoint :as entrypoint]))

(defonce reactor (atom nil))

(defn ^:dev/after-load ^:export start! []
  (app/start! reactor entrypoint/Main))

(defn ^:dev/before-load stop! []
  (app/stop! reactor))
