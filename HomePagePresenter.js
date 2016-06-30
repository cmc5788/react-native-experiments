'use strict';

import Rx from 'rxjs';

const Observable = Rx.Observable;
const fromPromise = Observable.fromPromise;
const timer = Observable.timer;

function HomePagePresenter() {

  this.buttonClicked = () => {
    console.log('HomePagePresenter buttonClicked');

    this.unsub('buttonClickedActionSub');
    this.sub('buttonClickedActionSub',
      this.view.sendStateObs('setButtonColor', '#0000FF')
      .flatMap(() => timer(500))
      .flatMap(() => this.view.sendStateObs('setButtonColor', '#00FF00'))
      .flatMap(() => timer(500))
      .flatMap(() => this.view.sendStateObs('setButtonColor', '#FF00FF'))
      .flatMap(() => timer(500))
      .flatMap(() => this.view.sendStateObs('setImageUrl', 'https://vinli-public.s3.amazonaws.com/app-catalog/home-connect/home-icon.png'))
      .flatMap(() => timer(500))
      .flatMap(() => this.net.fetchJsonObs('https://auth.vin.li/api/v1/config'))
      .flatMap((json) => this.nav.recvJsonObs(json))
      .flatMap(() => Observable.throw('Test Error!'))
      .catch((error) => Observable.of(console.log(error)))
      .flatMap(() => this.nav.navigateObs(this.nav.DETAIL_PAGE, 'HALLO')),
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
    console.log('HomePagePresenter afterRestore');
    this.view.restoreSentState();
  };

  this.otherCreate = (other) => {
    console.log('HomePagePresenter otherCreate');
    if (other.tagBase === this.nav.DETAIL_PAGE &&
        other.tagExtras === 'HALLO') {
      other.initFromHome();
    }
  };
}

module.exports = HomePagePresenter;
