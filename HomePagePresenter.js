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
      this.view.sendObs({ setButtonColor: '#0000FF' })
      .flatMap(() => timer(1000))
      .flatMap(() => this.view.sendObs({ setButtonColor: '#00FF00' }))
      .flatMap(() => timer(1000))
      .flatMap(() => this.view.sendObs({ setButtonColor: '#FF00FF' }))
      .flatMap(() => timer(1000))
      .flatMap(() => this.view.sendObs({ setImageUrl: 'https://vinli-public.s3.amazonaws.com/app-catalog/home-connect/home-icon.png' }))
      .flatMap(() => timer(1000))
      .flatMap(() => this.net.fetchJsonObs('https://auth.vin.li/api/v1/config'))
      .flatMap((json) => this.nav.recvJsonObs(json))
      .flatMap(() => Observable.throw('Test Error!'))
      .catch((error) => Observable.of(console.log(error)))
      .flatMap(() => this.nav.navigateObs(this.nav.DetailPage, this.nav.FORWARD, 'meta')),
        success => console.log(`Observable success`),
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
}

module.exports = HomePagePresenter;
