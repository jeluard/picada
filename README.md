# Picada [![License](http://img.shields.io/badge/license-EPL-blue.svg?style=flat)](https://www.eclipse.org/legal/epl-v10.html)

[Usage](#usage) | [Components](#components) | [Theme](#theme) | [Browser support](#browser-support)

A [Material](http://www.google.com/design/spec/) inspired collection of HTML elements (Custom Elements) implemented in ClojureScript.

[Demo](https://jeluard.github.io/picada/)

[![Clojars Project](http://clojars.org/picada/latest-version.svg)](http://clojars.org/picada).

## Usage

Picada components are native HTML elements that can be manipulated like any HTML element (e.g. `<div/>`).

```
(ns my-app
  (:require [picada.bootstrap :as boot]
            [picada.component.button :as but]
            [picada.component.icon :as ico]
            [lucuma.core :as luc]))

; register all elements at once
(boot/register-all)

; or individually
(luc/register [but/pica-fab ico/pica-icon])

; then use them

(.appendChild (.-body js/document) "pica-fab")
```

Any webpage using Picada components must import associated `JS` and `CSS` assets.

```html
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
  <link rel="stylesheet" href="picada.css">
  <script src="picada.js"></script>
</head>
<body>
  <pica-fab icon="reply" mini></pica-fab>
</body>
</html>
```

Some components are intended to be used programmatically:

```clojure
(ns my-app
  (:require [picada.component.snackar :as sna]))

(defn show-snackbar []
  (sna/show "Some text" {:name "An action" :fn #(.log js/console "Action clicked")}))
```

Also as some component properties are not exposed as attributes (e.g. complex values such as maps) they must be manipulated programmatically.

```html
<pica-fab id="fab-with-action" icon="reply"></pica-fab>
<script>
  var el = document.getElementById("fab-with-action");
  el.action = {name: "My Action", fn: function() {alert("You clicked the button!")}};
</script>
```

## Components

### Icons

Components using icon rely on [Material Icons](http://www.google.com/design/icons/). Any icon in this catalog can be specified using its id.

```html
<pica-fab icon="reply" mini></pica-fab>
<!-- will use http://www.google.com/design/icons/#ic_reply -->
```

### Create a component

Picada depends on [lucuma](https://github.com/jeluard/lucuma) for the Custom Elements creation, [hipo](https://github.com/jeluard/hipo) to facilitate eventual DOM reconciliations and [garden](https://github.com/noprompt/garden) to define elements styles as CSS.

```clojure
(ns my-ns
  (:require-macros [picada.component :refer [defcomponent]]))

(defcomponent my-component
  :document
  (fn [el {:keys [action title]}]
    [:host {:role "dialog" :title (str "::" title)}
      [:h2 "My component"]
      [:div (if action (:name action))]])
  :style
  [:host
    {:color "red"}]
  :properties {:action nil :title ""})
```

## Theme

Each component embed its own styles as CSS. Those are parameterized using CSS variables.

## Browser support

Picada relies on modern technology ([CSS Animations](http://www.w3.org/TR/css3-animations/), CSS4 and DOM4) that might not be available in your target browsers.

A combination of polyfills and transpiling will help with older browsers:

* [web-animation-js](https://github.com/web-animations/web-animations-js/) to polyfill CSS Animations
* [dom4](https://github.com/WebReflection/dom4) (available as [CLJSJS package](https://github.com/cljsjs/packages/tree/master/dom4)) to polyfill DOM4 methods
* [document-register-element](https://github.com/WebReflection/document-register-element) to polyfill Custom Elements (see more details [here](https://github.com/jeluard/lucuma#browser-support))
* [cssnext](http://cssnext.io) to transpile CSS4

## License

Copyright (C) 2015 Julien Eluard

Distributed under the Eclipse Public License, the same as Clojure.
