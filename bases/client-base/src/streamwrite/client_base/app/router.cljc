(ns streamwrite.client-base.app.router
  (:require [hyperfiddle.electric :as e]
            [streamwrite.frontend-router.api :as frontend-router]
            [missionary.core :as ms]
            [streamwrite.client-base.app.layout :as layout]
            [streamwrite.client-base.app.home.views :as home.views]
            [streamwrite.client-base.app.project.views :as projects.views]
            [streamwrite.client-base.app.home.routes :as home.routes]
            [streamwrite.client-base.app.project.routes :as projects.routes]
            [potpuri.core :as pt]
            #?@(:cljs [[reitit.frontend :as rf]
                       [reitit.frontend.easy :as rfe]
                       [reitit.coercion.malli :as rcm]])))

(e/def routes
  [["/app" {:layout layout/MainViewLayout}
    ["" {:name        :root
         :redirect-to [:home]}]
    ["not-found" {:name  :not-found
                  :view  layout/NotFoundView
                  :title "Not Found"}]
    home.routes/routes
    projects.routes/routes]])

(e/def rf-router
  (e/client
    (rf/router routes {:data      {:coercion rcm/coercion}
                       :conflicts false})))

(e/def app-router
  (e/client
    (->> (ms/observe
           (fn [!]
             (rfe/start!
               rf-router
               !
               {:use-fragment false})))
         (ms/relieve {})
         new)))

(e/defn RouteSwitch []
  (e/client
    (let [{:keys [layout view title]} frontend-router/data]
      (e/client (layout. {:view  view
                          :title (if (string? title)
                                   title
                                   (title. frontend-router/match))})))))
