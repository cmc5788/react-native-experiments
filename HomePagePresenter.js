'use strict';

const wait = (time) => new Promise((resolve) => {
  setTimeout(() => {
    resolve();
  }, time);
});

function HomePagePresenter() {

  this.buttonClicked = (evt) => {
    console.log('HomePagePresenter buttonClicked');

    this.view.send({ setButtonColor: '#0000FF' }) // BLUE
      .then(() => wait(1000))
      .then(() => this.view.send({ setButtonColor: '#00FF00' })) // GREEN
      .then(() => wait(1000))
      .then(() => this.view.send({ setButtonColor: '#FF00FF' })) // MAGENTA
      .then(() => wait(1000))
      .then(() => this.view.send({ setImageUrl: 'https://vinli-public.s3.amazonaws.com/app-catalog/home-connect/home-icon.png' }))
      .then(() => wait(2000))
      .then(() => fetch('https://auth.vin.li/api/v1/config'))
      .then((response) => response.json())
      .then((responseJson) => this.nav.recvJson(responseJson))
      .catch((error) => console.warn(error))
      .then(() => this.nav.navigate("DetailPageView", 1, "meta"))
      // .then(() => this.nav.goBack())
      // .then((wentBack) =>
      //   console.log(`goBack went back? ${JSON.stringify(wentBack)}`));
  };

  this.init = () => {
    console.log('HomePagePresenter init');
  };

  this.back = () => {
    console.log('HomePagePresenter back');
  },

  this.destroy = () => {
    console.log('HomePagePresenter destroy');
  };
}

module.exports = HomePagePresenter;
