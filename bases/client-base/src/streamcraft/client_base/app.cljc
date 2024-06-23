(ns streamcraft.client-base.app
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [streamcraft.client-base.app.layout :as layout]
            [streamcraft.client-base.app.router :as app-router]
            [streamcraft.frontend-router.api :as frontend-router]))

(e/defn App [ring-request]
  (e/client
    (let [match app-router/app-router]
      (binding [dom/node js/document.body
                frontend-router/router app-router/rf-router
                frontend-router/match match
                frontend-router/data (some-> match :data)
                frontend-router/name (some-> match :data :name)]
        (layout/MainPageLayout.
          (e/fn [] (app-router/RouteSwitch.)))))))