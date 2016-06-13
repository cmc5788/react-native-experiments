import InitApp from './init';
import Navigator from './MyNavigator';

InitApp({
  emptyView: 'HomePageView',

  init: () => {
    console.log('got init');
  },

  pause: () => {
    console.log('got pause');
    Navigator.stack().then((stack) => {
      console.log(`got stack ${JSON.stringify(stack)}`);
    });
  },
  
  resume: () => {
    console.log('got resume');
  }
});
