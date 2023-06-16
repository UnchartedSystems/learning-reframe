(ns reddit-viewer.chart
    (:require ["react" :as react]
              [reagent.core :as r]
              [reagent.dom :as rdom]
              [re-frame.core :as rf]
              ["chart.js" :as chartjs]))

(defn render-data [node data]
  (chartjs/Chart.
    node
    (clj->js
      {:type    "bar"
       :data    {:labels   (map :title data)
                 :datasets [{:label "votes"
                             :data  (map :score data)}]}
       :options {:scales {:xAxes [{:display false}]}}})))

(defn destroy-chart [chart]
  (when @chart
    (.destroy @chart)
    (reset! chart nil)))

(defn render-chart [chart]
  (fn [component]
    (when-let [posts @(rf/subscribe [:posts])]
      (let [node (rdom/dom-node component)]
        (destroy-chart chart)
        (reset! chart (render-data node posts))))))

(defn render-canvas []
  (when @(rf/subscribe [:posts]) [:canvas]))

(defn chart-posts-by-votes [data]
  (let [chart (atom nil)]
    (r/create-class
      {:component-did-mount    (render-chart chart)
       :component-did-update   (render-chart chart)
       :component-will-unmount (fn [_] (destroy-chart chart))
       :render                 render-canvas})))
