(ns reddit-viewer.core
    (:require [reagent.core :as r]
              [reagent.dom :as rdom]
              [re-frame.core :as rf]
              [reddit-viewer.events]
              [ajax.core :as ajax]
              [reddit-viewer.chart :as chart]))

(defn display-post [{:keys [permalink num_comments subreddit title score url]}]
  [:div.card.m-2
   [:div.card-block
    [:h4.card-title
     [:a {:href (str "http://reddit.com" permalink)} title " "]]
    [:div [:span.badge.badge-info
           {:color "info"}
           subreddit " score " score " / comments " num_comments]]
    [:img {:width "300px" :src url}]]])

(defn display-posts [posts]
  (when-not (empty? posts)
    [:div
     (for [posts-row (partition-all 3 posts)]
       ^{:key posts-row}
       [:div.row
        (for [post posts-row]
          ^{:key post}
          [:div.col-4 [display-post post]])])]))

(defn sort-posts [title sort-key]
  [:button.btn.btn-secondary
   {:on-click #(rf/dispatch [:sort-posts sort-key])}
   (str "sort posts by " title)])

(defn navitem [title view id]
  [:li.nav-item
   {:class-name (when (= id view) "active")}
   [:a.nav-link
    {:href     "#"
     :on-click #(rf/dispatch [:select-view id])}
    title]])

(defn navbar [view]
  [:nav.navbar.navbar-toggleable-md.navbar-light.bg-faded
   [:ul.navbar-nav.mr-auto.nav
    {:className "navbar-nav mr-auto"}
    [navitem "Posts" view :posts]
    [navitem "Chart" view :chart]]])

(defn home-page []
  (let [view @(rf/subscribe [:view])]
    [:div
     [navbar view]
     [:div.card>div.card-block
      [:div.btn-group
       [sort-posts "score" :score]
       [sort-posts "comments" :num_comments]]
      (case view
        :chart [chart/chart-posts-by-votes]
        :posts [display-posts @(rf/subscribe [:posts])])]]))

(defn mount-root []
  (rdom/render [home-page] (.-body js/document)))

(defn init! []
  (rf/dispatch-sync [:init-db])
  (rf/dispatch [:load-posts "http://www.reddit.com/r/Catloaf.json?sort=new&limit=50"])
  (mount-root))
