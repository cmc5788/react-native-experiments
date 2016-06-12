import { requireNativeComponent, View } from 'react-native';

var iface = {
  name: 'MyAppRoot',
  propTypes: {
    ...View.propTypes // include the default view properties
  }
};

module.exports = requireNativeComponent('MyAppRoot', iface);
