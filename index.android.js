/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  ToastAndroid,
  DeviceEventEmitter,
  AppRegistry//,
  // StyleSheet,
  // Text,
  // View
} from 'react-native';
// import Shared from './shared';
import MyAppRoot from './MyAppRoot';
import Navigator from './MyNavigator';

// class AwesomeProject extends Component {
//   render() {
//
//     Navigator.navigate("hello");
//
//     // return (
//     //   <View style={styles.container}>
//     //     <HWText>
//     //     </HWText>
//     //     <Text style={styles.welcome}>
//     //       Welcome to React Native!
//     //     </Text>
//     //     <Text style={styles.instructions}>
//     //       To get started, edit {Shared()} index.android.js
//     //     </Text>
//     //     <Text style={styles.instructions}>
//     //       Shake or press menu button for dev menu
//     //     </Text>
//     //   </View>
//     // );
//     // console.log(new MyAppRoot({style: {flex: 1}}));
//     return <MyAppRoot style={{ flex: 1 }}/>;
//   }
// }

// const styles = StyleSheet.create({
//   container: {
//     flex: 1,
//     justifyContent: 'center',
//     alignItems: 'center',
//     backgroundColor: '#F5FCFF',
//   },
//   welcome: {
//     fontSize: 20,
//     textAlign: 'center',
//     margin: 10,
//   },
//   instructions: {
//     textAlign: 'center',
//     color: '#333333',
//     marginBottom: 5,
//   },
// });

AppRegistry.registerComponent('AwesomeProject', () => class extends Component {
  // render = () => (<MyAppRoot style={{ flex: 1 }}/>)
  render = () => React.createElement(MyAppRoot, { style: { flex: 1 } })

  componentWillMount = () => {
    DeviceEventEmitter.addListener('onAppInit', (evt) => {
      console.log('got onAppInit');
      ToastAndroid.show('onAppInit', ToastAndroid.SHORT);
    });
    DeviceEventEmitter.addListener('onAppResume', (evt) => {
      console.log('got onAppResume');
      ToastAndroid.show('onAppResume', ToastAndroid.SHORT);
    });
    DeviceEventEmitter.addListener('onAppPause', (evt) => {
      console.log('got onAppPause');
      ToastAndroid.show('onAppPause', ToastAndroid.SHORT);
    });
    DeviceEventEmitter.addListener('onAppDestroy', (evt) => {
      console.log('got onAppDestroy');
      ToastAndroid.show('onAppDestroy', ToastAndroid.LONG);
    });
  }
});
