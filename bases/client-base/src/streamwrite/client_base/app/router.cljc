(ns streamwrite.client-base.app.router
  (:require [hyperfiddle.electric :as e]
            [streamwrite.frontend-router.api :as frontend-router]
            [missionary.core :as ms]
            [streamwrite.client-base.app.layout :as layout]
            [streamwrite.client-base.app.home.core :as home]
            [streamwrite.client-base.app.projects.core :as projects]
            [potpuri.core :as pt]
            #?@(:cljs [[streamwrite.client-base.app.routes :refer [routes]]
                       [reitit.frontend :as rf]
                       [reitit.frontend.easy :as rfe]
                       [reitit.coercion.malli :as rcm]])))

#?(:cljs (def rf-router (rf/router routes {:data      {:coercion rcm/coercion}
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
    (let [{:keys [parameters]} frontend-router/match
          {:keys [path]} parameters]
      (case frontend-router/name
        :root (rfe/navigate :home)
        :home (layout/MainViewLayout. {:view        home/HomeView
                                        :title       "Home"
                                        :breadcrumbs nil})
        :projects (layout/MainViewLayout. {:view        projects/ProjectsView
                                            :title       "Projects"
                                            :breadcrumbs [:home]})
        :project (let [{:keys [id]} path
                       project-name (e/server (-> projects/projects
                                                  (pt/find-first {:id id})
                                                  :name))]
                   (if project-name
                     (layout/MainViewLayout. {:view        projects/ProjectView
                                               :title       project-name
                                               :breadcrumbs [:home :projects]})
                     (rfe/navigate :not-found)))

        :project-documents (let [{:keys [id]} path
                                 project-name (e/server (-> projects/projects
                                                            (pt/find-first {:id id})
                                                            :name))]
                             (layout/MainViewLayout. {:view        projects/ProjectDocumentsView
                                                       :title       (str project-name " - Documents")
                                                       :breadcrumbs [:home :projects]}))
        :project-sources (let [{:keys [id]} path
                               project-name (e/server (-> projects/projects
                                                          (pt/find-first {:id id})
                                                          :name))]
                           (layout/MainViewLayout. {:view        projects/ProjectSourcesView
                                                     :title       (str project-name " - Sources")
                                                     :breadcrumbs [:home :projects]}))
        :project-new (layout/MainViewLayout. {:view        projects/NewProjectView
                                               :title       "New Project"
                                               :breadcrumbs [:home :projects]})
        (layout/MainViewLayout. {:view        layout/NotFoundView
                                  :title       "Not Found"
                                  :breadcrumbs nil})))))
