'use strict';

module.exports = (buildPresenter) => {
  const me = { };
  const presenter = buildPresenter(me);
  for (var attr in presenter) {
    me[attr] = presenter[attr];
  }
  return me;
};
