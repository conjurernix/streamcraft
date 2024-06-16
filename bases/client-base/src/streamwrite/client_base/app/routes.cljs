(ns streamwrite.client-base.app.routes
  (:require [streamwrite.client-base.app.home.routes :as home]
            [streamwrite.client-base.app.layout :as layout]
            [streamwrite.client-base.app.projects.routes :as projects]))

(def routes
  [["/app" {:layout layout/MainViewLayout}
    ["" {:name        :root
         :redirect-to [:home]}]
    ["not-found" {:name :not-found
                  :view layout/NotFoundView
                  :title "Not Found"}]
    home/routes
    projects/routes]])
