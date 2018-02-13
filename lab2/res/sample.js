/**
 * Created by alex on 26.10.17.
 */

var apiServHost = "http://10.42.0.1:8008";

function createTree($place, tree) {
  if (!$place instanceof jQuery) {
    throw new Error("Invalid 1st argument. It must be instance of jQuery");
  }

  var queue = [];
  var $parent = $place,
    $icon = null,
    tmp,
    next;

  var elem = tree;
  var start = true;

  var x = .51e-10;

  while (start || queue.length !== 0) {
    if (queue.length > 0) {
      next = queue.shift();
      $parent = next.jQuery;
      elem = next.elem;
      if (start) start = false;
    }
    if (!start) {
      $icon = $("<a><span class=\"fa category\"></span></a>");
      $parent.prepend($icon);
    }
    if (elem.hasOwnProperty("categories") && elem.categories.length > 0) {
      if ($icon) {
        $icon.children("span").addClass("has-child");
      }
      tmp = $("<ul></ul>").appendTo($parent);
      for (var i in elem["categories"]) {
        var child = elem["categories"][i];
        var $child = $("<li></li>").html("<a>" + child.name + "</a>").data("id", child.id);
        tmp.append($child);
        queue.push({ elem: child, jQuery: $child });
      }
    }
  }
}
