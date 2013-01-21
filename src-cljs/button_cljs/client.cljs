(ns button-cljs.core)

(def game-state (atom {}))

(defn init-game []
  (swap! game-state assoc :button-on true :hit 0 :miss 0))

(defn show-scores []
  (let [state (deref game-state)]
    (js/alert (str "Hits: " (:hit (deref game-state)) "\n"
                   "Misses: " (:miss (deref game-state))))))

(def score (.getElementById js/document "score"))
(.addEventListener score "click" show-scores)

(def controls
  {32, #(swap! game-state
               (fn [s]
                 (let [score (if (:button-on s) :hit :miss)]
                   (assoc s score (inc (get s score))))))
   70, #(swap! game-state
               (fn [s]
                 (assoc s :button-on (not (:button-on s)))))
   82, init-game})

(defn key-handler
  [key]
  (let [action (get controls (.-keyCode key))]
    (when action (action))))

(.addEventListener js/document "keydown" key-handler)

(init-game)
