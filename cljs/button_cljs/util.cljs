;;----------------------------------------------------------------------
;; File util.cljs
;; Written by Chris
;; 
;; Created 23 Jan 2013
;; Last modified 27 Jan 2013
;; 
;; Defines utility functions for the button game.
;;----------------------------------------------------------------------

(ns button-cljs.util)

;;----------------------------------------
;; Miscellaneous
;;----------------------------------------

(defn cur-time [] (.getTime (js/Date.)))
