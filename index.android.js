import Init from './init';
import Navigator from './MyNavigator';

const app = {

  init() {
    console.log('got init');
  },

  pause() {
    console.log('got pause');
    Navigator.stack().then((stack) => {
      console.log(`got stack ${JSON.stringify(stack)}`);
    });
  },

  resume() {
    console.log('got resume');
  },

  emptyView: 'HomePageView',

  viewPresenters: {
    'HomePageView': () => require('./HomePagePresenter'),
    'DetailPageView': () => require('./DetailPagePresenter')
  }

};

Init(app);
