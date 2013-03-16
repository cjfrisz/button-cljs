;;----------------------------------------------------------------------
;; File globals.cljs
;; Written by Chris Frisz
;; 
;; Created 21 Feb 2013
;; Last modified 21 Feb 2013
;; 
;; 
;;----------------------------------------------------------------------

(ns button-cljs.globals)

(def game-fps 60)

(def canvas-id "gameCanvas")

(def game-state (atom nil))

(def button-width 50)
(def button-height 50)
(def button-x 225)
(def button-y 225)
(def button-on-color "#FF0000")
(def button-off-color "#800000")

(def canvas-width 500)
(def canvas-height 500)
(def canvas-style "border:1px solid #FFFFFF;")
(def canvas-background "#000000")
