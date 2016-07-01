'use strict';

function DetailPagePresenter() {

  this.init = () => {
    console.log(`DetailPagePresenter init ${JSON.stringify(this.view.state)}`);
    this.view.send({ setLabelText:
      `${this.tagExtras} : ${this.fromHome ? 'from home' : 'not from home'}` });
  };

  this.save = () => {
    console.log('DetailPagePresenter save');
  };

  this.destroy = () => {
    console.log('DetailPagePresenter destroy');
  };

  this.back = () => {
    console.log('DetailPagePresenter back');
  };

  this.initFromHome = () => {
    this.fromHome = true;
  };

  this.labelClicked = () => {
    console.log('DetailPagePresenter labelClicked');
    this.nav.navigate(this.nav.DETAIL_PAGE, `${this.tagExtras}x`);
  };

  this.goBackClicked = () => {
    console.log('DetailPagePresenter goBackClicked');
    this.nav.goBack();
  };
}

module.exports = DetailPagePresenter;
