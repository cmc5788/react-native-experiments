'use strict';

import Rx from 'rxjs';

const Observable = Rx.Observable;
const fromPromise = Observable.fromPromise;
const timer = Observable.timer;

function HomePagePresenter() {

  this.setButtonColorObs = (color) => {
    return Observable.defer(() => {
      return Observable.of(this.state.buttonColor = color);
    })
    .flatMap((c) => this.view.sendObs({ setButtonColor: c }));
  };

  this.buttonClicked = () => {
    console.log('HomePagePresenter buttonClicked');

    this.unsub('buttonClickedActionSub');
    this.sub('buttonClickedActionSub',
      this.setButtonColorObs('#0000FF')
      .flatMap(() => timer(500))
      .flatMap(() => this.setButtonColorObs('#00FF00'))
      .flatMap(() => timer(500))
      .flatMap(() => this.setButtonColorObs('#FF00FF'))
      .flatMap(() => timer(500))
      .flatMap(() => this.view.sendObs({ setImageUrl: 'https://vinli-public.s3.amazonaws.com/app-catalog/home-connect/home-icon.png' }))
      .flatMap(() => timer(500))
      .flatMap(() => this.net.fetchJsonObs('https://auth.vin.li/api/v1/config'))
      .flatMap((json) => this.nav.recvJsonObs(json))
      .flatMap(() => Observable.throw('Test Error!'))
      .catch((error) => Observable.of(console.log(error)))
      .flatMap(() => this.nav.navigateObs(this.nav.DETAIL_PAGE, 'HALLO', 'meta')),
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

  this.beforeSave = () => {
    console.log('HomePagePresenter beforeSave');
  };

  this.afterRestore = () => {
    console.log(`HomePagePresenter afterRestore ${this.state.blah}`);
    if (this.state.buttonColor) {
      this.view.send({ setButtonColor: this.state.buttonColor });
    }
  };
}

module.exports = HomePagePresenter;
