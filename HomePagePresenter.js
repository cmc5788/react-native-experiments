'use strict';

import Rx from 'rxjs';

const Observable = Rx.Observable;
const fromPromise = Observable.fromPromise;
const timer = Observable.timer;

function HomePagePresenter() {

  this.sendStateObs = (key, val) => {
    const sendObj = { };
    sendObj[key] = val;
    return Observable.defer(() => {
      this.state[`sendState___${key}`] = val;
      return Observable.of(null);
    })
    .flatMap(() => this.view.sendObs(sendObj));
  };

  this.restoreSentState = () => {
    let sendObj;
    for (var stateProp in this.state) {
      if (stateProp.startsWith('sendState___')) {
        if (!sendObj) sendObj = { };
        sendObj[stateProp.substring('sendState___'.length)] =
          this.state[stateProp];
      }
    }
    if (sendObj) {
      this.view.send(sendObj);
    }
  };

  this.buttonClicked = () => {
    console.log('HomePagePresenter buttonClicked');

    this.unsub('buttonClickedActionSub');
    this.sub('buttonClickedActionSub',
      this.sendStateObs('setButtonColor', '#0000FF')
      .flatMap(() => timer(500))
      .flatMap(() => this.sendStateObs('setButtonColor', '#00FF00'))
      .flatMap(() => timer(500))
      .flatMap(() => this.sendStateObs('setButtonColor', '#FF00FF'))
      .flatMap(() => timer(500))
      .flatMap(() => this.sendStateObs('setImageUrl', 'https://vinli-public.s3.amazonaws.com/app-catalog/home-connect/home-icon.png'))
      .flatMap(() => timer(500))
      .flatMap(() => this.net.fetchJsonObs('https://auth.vin.li/api/v1/config'))
      .flatMap((json) => this.nav.recvJsonObs(json))
      .flatMap(() => Observable.throw('Test Error!'))
      .catch((error) => Observable.of(console.log(error)))
      .flatMap(() => this.nav.navigateObs(this.nav.DETAIL_PAGE, 'HALLO', 'meta')),
        success => console.log(`buttonClicked success`),
        err => console.log(err)
      );
  };

  this.init = () => {
    console.log('HomePagePresenter init');
  };

  this.back = () => {
    console.log('HomePagePresenter back');
  };

  this.destroy = () => {
    console.log('HomePagePresenter destroy');
    this.unsub('buttonClickedActionSub');
  };

  this.beforeSave = () => {
    console.log('HomePagePresenter beforeSave');
  };

  this.afterRestore = () => {
    console.log(`HomePagePresenter afterRestore ${this.state.blah}`);
    this.restoreSentState();
  };
}

module.exports = HomePagePresenter;
