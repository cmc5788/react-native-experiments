import Init from './init';
import Navigator from './MyNavigator';

// TODO - make app presenter structure consistent with view presenters
const app = {

  init() {
    console.log('app init');
  },

  pause() {
    console.log('app pause');
    Navigator.stack().then((stack) => {
      console.log(`got stack ${JSON.stringify(stack)}`);
    });
  },

  resume() {
    console.log('app resume');
  },

  back() {
    console.log('app back');
  },

  emptyView: 'HomePageView',

  viewPresenters: {
    'HomePageView': () => require('./HomePagePresenter'),
    'DetailPageView': () => require('./DetailPagePresenter')
  }

};

Init(app);
