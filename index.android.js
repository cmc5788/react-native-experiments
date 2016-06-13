import InitApp from './init';

InitApp({
  emptyView: 'HomePageView',
  init: () => {
    console.log('got init');
  },
  pause: () => {
    console.log('got pause');
  },
  resume: () => {
    console.log('got resume');
  }
});
