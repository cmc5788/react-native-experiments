'use strict';

module.exports = (presenterFunc, tag, view) => {
  const presenter = new presenterFunc();
  presenter.tag = tag;
  presenter.view = view;
  return presenter;
};
