(ns reddit-viewer.core
    (:require [reagent.core :as r]
              [reagent.dom :as rdom]
              [ajax.core :as ajax]))

(defonce posts (r/atom nil))

(defn find-posts-with-preview [posts]
  (filter #(= (:post_hint %) "image") posts))

(defn load-posts []
  (ajax/GET "http://www.reddit.com/r/Catloaf.json?sort=new&limit=10"
            {:handler         #(->> (get-in % [:data :children])
                                    (map :data)
                                    (find-posts-with-preview)
                                    (reset! posts))
             :response-format :json
             :keywords?       true}))

(load-posts)
(defn home-page []
  [:div [:h2 "Welcome to Reagent"]])

(defn mount-root []
  (rdom/render [home-page] (.-body js/document)))



(defn init! []
  (mount-root))



;; (defn root-element []
;;   [:div.mx-5.my-5
;;    [:div #_{:class [:container "max-w-[73ch]"]}
;;     [game/main]]])

;; (defn ^:dev/after-load start []
;;   (rdom/render
;;    [:div {:class "bg-[#fff] w-full"} [root-element]]
;;    (.-body js/document)))

;; (defn init []
;;   (start))

;; (defn ^:dev/before-load stop []
;;   #_(js/console.log "stop"))
