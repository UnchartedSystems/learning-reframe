(ns reddit-viewer.chart
    (:require [reagent.core :as r]
              [cljsjs.chartjs]))

(defn render-data [node data]
  (js/Chart.
   node
   (clj->js
    {:type "bar"
     :data {:labels (map :title data)
            :datasets [{:label "votes"
                        :data (map :score data)}]}
     :options {:scales {:xAxes [{:display false}]}}})))
