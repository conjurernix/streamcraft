(ns streamwrite.client-base.app.project.routes
  (:require [hyperfiddle.electric :as e]
            [potpuri.core :as pt]
            [streamwrite.client-base.app.project.views :as views]))

(e/def routes
  (e/client
    ["/projects" {:category :projects}
     ["" {:name  :projects
          :title "Projects"
          :view  views/ProjectsView}]
     ["/new" {:name  :project-new
              :title "New Project"
              :view  views/NewProjectView}]
     ["/:id" {:secondary-sidenav :projects
              :parameters        {:path [:map
                                         [:id :uuid]]}}
      ["" {:name  :project
           :title (e/fn [match]
                    (e/client
                      (let [{:keys [parameters]} match
                            {:keys [path]} parameters
                            {:keys [id]} path
                            project-name (e/server
                                           (-> views/projects
                                               (pt/find-first {:id id})
                                               :name))]
                        project-name)))
           :view  views/ProjectView}]
      ["/documents" {:name  :project-documents
                     :title (e/fn [match]
                              (e/client
                                (let [{:keys [parameters]} match
                                      {:keys [path]} parameters
                                      {:keys [id]} path
                                      project-name (e/server
                                                     (-> views/projects
                                                         (pt/find-first {:id id})
                                                         :name))]
                                  (str project-name " - Documents"))))
                     :view  views/ProjectDocumentsView}]
      ["/sources" {:name  :project-sources
                   :title (e/fn [match]
                            (e/client
                              (let [{:keys [parameters]} match
                                    {:keys [path]} parameters
                                    {:keys [id]} path
                                    project-name (e/server
                                                   (-> views/projects
                                                       (pt/find-first {:id id})
                                                       :name))]
                                (str project-name " - Sources"))))
                   :view  views/ProjectSourcesView}]]]))
