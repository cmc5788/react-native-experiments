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

  emptyView: Navigator.HOME_PAGE,

  viewPresenters: new function() {
    this[Navigator.HOME_PAGE] = () => require('./HomePagePresenter');
    this[Navigator.DETAIL_PAGE] = () => require('./DetailPagePresenter');
  }()

};

Init(app);
