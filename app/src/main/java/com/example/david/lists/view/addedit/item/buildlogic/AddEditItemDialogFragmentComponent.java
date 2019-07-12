package com.example.david.lists.view.addedit.item.buildlogic;

import android.app.Application;

import androidx.fragment.app.Fragment;

import com.example.david.lists.common.buildlogic.ViewCommonModule;
import com.example.david.lists.common.buildlogic.ViewScope;
import com.example.david.lists.view.addedit.common.buildlogic.AddEditDialogFragmentCommonModule;
import com.example.david.lists.view.addedit.item.AddEditItemDialogFragment;

import javax.inject.Named;

import dagger.BindsInstance;
import dagger.Component;

import static com.example.david.lists.view.addedit.common.buildlogic.AddEditNamedConstants.ID;
import static com.example.david.lists.view.addedit.common.buildlogic.AddEditNamedConstants.TITLE;
import static com.example.david.lists.view.addedit.common.buildlogic.AddEditNamedConstants.USER_LIST_ID;

@ViewScope
@Component(modules = {
        AddEditItemDialogFragmentModule.class,
        AddEditDialogFragmentCommonModule.class,
        ViewCommonModule.class
})
public interface AddEditItemDialogFragmentComponent {
    void inject(AddEditItemDialogFragment fragment);

    @Component.Builder
    interface Builder {
        AddEditItemDialogFragmentComponent build();

        @BindsInstance
        Builder application(Application application);

        @BindsInstance
        Builder fragment(Fragment fragment);

        @BindsInstance
        Builder id(@Named(ID) String id);

        @BindsInstance
        Builder title(@Named(TITLE) String title);

        @BindsInstance
        Builder userListId(@Named(USER_LIST_ID) String userListId);
    }
}