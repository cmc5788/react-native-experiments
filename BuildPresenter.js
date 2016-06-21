'use strict';

import nav from './MyNavigator';

module.exports = (presenterFunc, tag, view) => {
  const presenter = new presenterFunc();
  presenter.tag = tag;
  presenter.view = view;
  presenter.nav = nav;
  return presenter;
};
