webpackJsonp([2],{

/***/ 0:
/***/ (function(module, exports, __webpack_require__) {

/*eslint-disable no-console */
__webpack_require__(14);

document.addEventListener("DOMContentLoaded", function () {
   // app.className = app.className.replace(/none/g, '');
}, false);

/***/ }),

/***/ 14:
/***/ (function(module, exports) {

!function (window) {
    var n = document.documentElement,
        rootfont,
        isMobile = true,
        i = document.createElement('style');
    n.firstElementChild.appendChild(i);

    function infinite() {
        // var docW = document.documentElement.clientWidth;
        var docW = window.innerWidth;

        if (!navigator.userAgent.match(/Android|BlackBerry|iPhone|iPad|iPod|Opera Mini|IEMobile/i)) {
            isMobile = false;
            document.getElementsByTagName('html')[0].style.width = 375 + 'px';
            document.getElementsByTagName('html')[0].style.maxHeight = 690 + 'px';
        }
        if (isMobile) {
            if (docW < 320) {
                docW = 320;
                rootfont = 100 / 750 * docW;
                i.innerHTML = 'html{font-size:' + rootfont + 'px!important;}';
            } else if (docW <= 750) {
                rootfont = 100 / 750 * docW;
                i.innerHTML = 'html{font-size:' + rootfont + 'px!important;}';
            } else {
                i.innerHTML = 'html{font-size:100px!important;}';
            }
        } else {
            i.innerHTML = 'html{font-size:50px!important;}';
        }
    }
    window.addEventListener('resize', function () {
        infinite();
    }, !1);

    window.addEventListener('pageshow', function (e) {
        // pageshow无论这个页面是新打开的还是在往返缓存中的，都会在这个页面显示的时候触发。新打开的会在load后触发。
        // event对象中有一个persisted属性，是true时代表是从往返缓存中恢复的。
        // 缓存完全保存了整个页面，包括JS的执行状态，这就意味着不会再触发load事件。
        // 防止此情况发生，做一个判断，执行字体设置函数
        e.persisted && infinite();
    }, !1), infinite();
}(window);

/***/ }),

/***/ 21:
/***/ (function(module, exports) {

/*
	MIT License http://www.opensource.org/licenses/mit-license.php
	Author Tobias Koppers @sokra
*/
// css base code, injected by the css-loader
module.exports = function(useSourceMap) {
	var list = [];

	// return the list of modules as css string
	list.toString = function toString() {
		return this.map(function (item) {
			var content = cssWithMappingToString(item, useSourceMap);
			if(item[2]) {
				return "@media " + item[2] + "{" + content + "}";
			} else {
				return content;
			}
		}).join("");
	};

	// import a list of modules into the list
	list.i = function(modules, mediaQuery) {
		if(typeof modules === "string")
			modules = [[null, modules, ""]];
		var alreadyImportedModules = {};
		for(var i = 0; i < this.length; i++) {
			var id = this[i][0];
			if(typeof id === "number")
				alreadyImportedModules[id] = true;
		}
		for(i = 0; i < modules.length; i++) {
			var item = modules[i];
			// skip already imported module
			// this implementation is not 100% perfect for weird media query combinations
			//  when a module is imported multiple times with different media queries.
			//  I hope this will never occur (Hey this way we have smaller bundles)
			if(typeof item[0] !== "number" || !alreadyImportedModules[item[0]]) {
				if(mediaQuery && !item[2]) {
					item[2] = mediaQuery;
				} else if(mediaQuery) {
					item[2] = "(" + item[2] + ") and (" + mediaQuery + ")";
				}
				list.push(item);
			}
		}
	};
	return list;
};

function cssWithMappingToString(item, useSourceMap) {
	var content = item[1] || '';
	var cssMapping = item[3];
	if (!cssMapping) {
		return content;
	}

	if (useSourceMap && typeof btoa === 'function') {
		var sourceMapping = toComment(cssMapping);
		var sourceURLs = cssMapping.sources.map(function (source) {
			return '/*# sourceURL=' + cssMapping.sourceRoot + source + ' */'
		});

		return [content].concat(sourceURLs).concat([sourceMapping]).join('\n');
	}

	return [content].join('\n');
}

// Adapted from convert-source-map (MIT)
function toComment(sourceMap) {
	// eslint-disable-next-line no-undef
	var base64 = btoa(unescape(encodeURIComponent(JSON.stringify(sourceMap))));
	var data = 'sourceMappingURL=data:application/json;charset=utf-8;base64,' + base64;

	return '/*# ' + data + ' */';
}


/***/ })

});