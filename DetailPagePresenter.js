'use strict';

function DetailPagePresenter() {

  this.initFromHome = () => {
    this.fromHome = true;
  };

  this.labelClicked = () => {
    console.log('DetailPagePresenter labelClicked');
    this.nav.navigate(this.nav.DETAIL_PAGE, this.tagExtras + 'x');
  };

  this.goBackClicked = () => {
    console.log('DetailPagePresenter goBackClicked');
    this.nav.goBack();
  };

  this.init = () => {
    console.log('DetailPagePresenter init');
    this.view.send({ setLabelText:
      `${this.tagExtras} : ${this.fromHome ? 'from home' : 'not from home'}` });
  };

  this.back = () => {
    console.log('DetailPagePresenter back');
  };

  this.destroy = () => {
    console.log('DetailPagePresenter destroy');
  };

  this.beforeSave = () => {
    console.log('DetailPagePresenter beforeSave');
    this.view.state.blah = this.tagExtras;
  };

  this.afterRestore = () => {
    console.log(`DetailPagePresenter afterRestore ${JSON.stringify(this.view.state)}`);
  };
}

module.exports = DetailPagePresenter;
