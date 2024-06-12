(ns streamcraft.admin-base.main
  (:require [streamcraft.web-app.core :as app]
            [streamcraft.admin-base.app :refer [App]]))

(defonce reactor (atom nil))

(defn ^:dev/after-load ^:export start! []
  (app/start! reactor App))

(defn ^:dev/before-load stop! []
  (app/stop! reactor))
