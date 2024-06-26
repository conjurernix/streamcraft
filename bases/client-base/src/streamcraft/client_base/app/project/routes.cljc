(ns streamcraft.client-base.app.project.routes
  (:require [hyperfiddle.electric :as e]
            [potpuri.core :as pt]
            [streamcraft.client-base.app.project.views :as views]))

(e/def routes
  (e/client
    ["/projects" {:category :projects}
     ["" {:name        :projects
          :title       "Projects"
          :view        views/ProjectsView
          :breadcrumbs [:home]}]
     ["/new" {:name        :project-new
              :title       "New Project"
              :view        views/NewProjectView
              :breadcrumbs [:home :projects]}]
     ["/:id" {:secondary-sidenav :projects
              :parameters        {:path [:map
                                         [:id :uuid]]}}
      ["" {:name        :project
           :title       (e/fn [match]
                          (e/client
                            (let [{:keys [parameters]} match
                                  {:keys [path]} parameters
                                  {:keys [id]} path
                                  project-name (e/server
                                                 (-> views/projects
                                                     (pt/find-first {:id id})
                                                     :name))]
                              project-name)))
           :view        views/ProjectView
           :breadcrumbs [:home :projects]}]
      ["/documents" {:name        :project-documents
                     :title       (e/fn [match]
                                    (e/client
                                      (let [{:keys [parameters]} match
                                            {:keys [path]} parameters
                                            {:keys [id]} path
                                            project-name (e/server
                                                           (-> views/projects
                                                               (pt/find-first {:id id})
                                                               :name))]
                                        (str project-name " - Documents"))))
                     :view        views/ProjectDocumentsView
                     :breadcrumbs [:home :projects]}]
      ["/sources" {:name        :project-sources
                   :title       (e/fn [match]
                                  (e/client
                                    (let [{:keys [parameters]} match
                                          {:keys [path]} parameters
                                          {:keys [id]} path
                                          project-name (e/server
                                                         (-> views/projects
                                                             (pt/find-first {:id id})
                                                             :name))]
                                      (str project-name " - Sources"))))
                   :view        views/ProjectSourcesView
                   :breadcrumbs [:home :projects]}]]]))
